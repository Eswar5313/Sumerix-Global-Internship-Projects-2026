package com.eswar.employeemanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.eswar.employeemanager.EmployeeManagerApp
import com.eswar.employeemanager.data.DepartmentEntity
import com.eswar.employeemanager.data.DepartmentStat
import com.eswar.employeemanager.data.EmployeeEntity
import com.eswar.employeemanager.data.EmployeeRepository
import com.eswar.employeemanager.data.SortOrder
import com.eswar.employeemanager.domain.EmployeeForm
import com.eswar.employeemanager.domain.EmployeeValidator
import com.eswar.employeemanager.domain.Field
import com.eswar.employeemanager.domain.ValidationResult
import kotlinx.coroutines.launch

data class EmployeeFilter(val query: String = "", val departmentCode: String = "", val sort: SortOrder = SortOrder.NAME)

/** One-shot UI events (snackbar / navigate back) that must not re-fire after rotation. */
class Event<out T>(private val content: T) {
    private var handled = false
    fun getContentIfNotHandled(): T? = if (handled) null else { handled = true; content }
    fun peek(): T = content
}

sealed class UiEvent {
    data class Saved(val id: Long, val isNew: Boolean) : UiEvent()
    data class Deleted(val employee: EmployeeEntity) : UiEvent()
    data class Error(val message: String) : UiEvent()
}

/**
 * MVVM: the ViewModel survives configuration changes, exposes LiveData state to the Fragments and
 * runs all database writes in viewModelScope (Room suspend functions run off the main thread).
 */
class EmployeeViewModel(private val repository: EmployeeRepository) : ViewModel() {

    private val _filter = MutableLiveData(EmployeeFilter())
    val filter: LiveData<EmployeeFilter> = _filter

    /** Re-queries Room automatically whenever search text, department or sort order changes. */
    val employees: LiveData<List<EmployeeEntity>> = _filter.switchMap { f ->
        repository.search(f.query, f.departmentCode, f.sort)
    }
    val departments: LiveData<List<DepartmentEntity>> = repository.departments
    val count: LiveData<Int> = repository.count
    val stats: LiveData<List<DepartmentStat>> = repository.departmentStats

    private val _formErrors = MutableLiveData<Map<Field, String>>(emptyMap())
    val formErrors: LiveData<Map<Field, String>> = _formErrors

    private val _events = MutableLiveData<Event<UiEvent>>()
    val events: LiveData<Event<UiEvent>> = _events

    fun setQuery(query: String) = update { copy(query = query) }
    fun setDepartment(code: String) = update { copy(departmentCode = code) }
    fun setSort(sort: SortOrder) = update { copy(sort = sort) }

    private inline fun update(change: EmployeeFilter.() -> EmployeeFilter) {
        val next = (_filter.value ?: EmployeeFilter()).change()
        if (next != _filter.value) _filter.value = next
    }

    fun observe(id: Long): LiveData<EmployeeEntity?> = repository.observe(id)

    suspend fun load(id: Long): EmployeeEntity? = repository.get(id)

    /** Validates the form; on success inserts/updates and emits [UiEvent.Saved]. */
    fun save(form: EmployeeForm): Boolean {
        return when (val result = EmployeeValidator.validate(form)) {
            is ValidationResult.Invalid -> { _formErrors.value = result.errors; false }
            is ValidationResult.Valid -> {
                _formErrors.value = emptyMap()
                viewModelScope.launch {
                    repository.save(result.entity)
                        .onSuccess { id -> _events.value = Event(UiEvent.Saved(id, form.id == 0L)) }
                        .onFailure { e -> _events.value = Event(UiEvent.Error(e.message ?: "Could not save")) }
                }
                true
            }
        }
    }

    fun delete(employee: EmployeeEntity) = viewModelScope.launch {
        repository.delete(employee)
        _events.value = Event(UiEvent.Deleted(employee))
    }

    fun undoDelete(employee: EmployeeEntity) = viewModelScope.launch { repository.restore(employee) }

    fun toggleFavourite(employee: EmployeeEntity) = viewModelScope.launch { repository.toggleFavourite(employee) }

    suspend fun exportCsv(): String = repository.exportCsv()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as EmployeeManagerApp
                EmployeeViewModel(app.repository)
            }
        }
    }
}
