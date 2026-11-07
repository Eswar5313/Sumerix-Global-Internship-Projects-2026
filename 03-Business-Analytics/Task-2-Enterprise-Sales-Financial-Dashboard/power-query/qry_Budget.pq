// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "reference\budget_2025.csv"), [Delimiter = ",", Encoding = 65001]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Typed    = Table.TransformColumnTypes(Promoted, {{"Year", Int64.Type}, {"MonthNo", Int64.Type}, {"Region", type text}, {"BudgetINR", type number}}, "en-US")
    // already in long format (Year, MonthNo, Region) — no unpivot needed; joined to the model with TREATAS in the [Budget] measure
in
    Typed
