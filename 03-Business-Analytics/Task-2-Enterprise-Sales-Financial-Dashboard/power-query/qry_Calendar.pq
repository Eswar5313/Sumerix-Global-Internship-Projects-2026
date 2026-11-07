// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    StartDate   = #date(2024, 1, 1),
    EndDate     = #date(2025, 12, 31),
    Dates       = List.Dates(StartDate, Duration.Days(EndDate - StartDate) + 1, #duration(1, 0, 0, 0)),
    AsTable     = Table.FromList(Dates, Splitter.SplitByNothing(), {"Date"}),
    Typed       = Table.TransformColumnTypes(AsTable, {{"Date", type date}}),
    Year        = Table.AddColumn(Typed, "Year", each Date.Year([Date]), Int64.Type),
    Quarter     = Table.AddColumn(Year, "Quarter", each "Q" & Text.From(Date.QuarterOfYear([Date])), type text),
    MonthNo     = Table.AddColumn(Quarter, "MonthNo", each Date.Month([Date]), Int64.Type),
    MonthName   = Table.AddColumn(MonthNo, "MonthName", each Date.ToText([Date], [Format = "MMM", Culture = "en-US"]), type text),
    MonthStart  = Table.AddColumn(MonthName, "MonthStart", each Date.StartOfMonth([Date]), type date),
    Weekday     = Table.AddColumn(MonthStart, "WeekdayName", each Date.ToText([Date], [Format = "ddd", Culture = "en-US"]), type text),
    IsWeekend   = Table.AddColumn(Weekday, "IsWeekend", each Date.DayOfWeek([Date], Day.Monday) >= 5, type logical),
    // Indian financial year runs April–March: Apr 2024 → FY25
    FY          = Table.AddColumn(IsWeekend, "FinancialYear", each "FY" & Text.From(Number.Mod(if [MonthNo] >= 4 then [Year] + 1 else [Year], 100)), type text)
in
    FY
