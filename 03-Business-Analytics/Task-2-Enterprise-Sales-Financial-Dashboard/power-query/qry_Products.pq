// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "reference\products.csv"), [Delimiter = ",", Encoding = 65001, QuoteStyle = QuoteStyle.Csv]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Trimmed  = Table.TransformColumns(Promoted, {}, Text.Trim),
    Typed    = Table.TransformColumnTypes(Trimmed, {{"ProductID", type text}, {"ProductName", type text}, {"Category", type text}, {"Tier", type text}, {"ListPriceINR", type number}, {"UnitCostINR", type number}}, "en-US"),
    NoErrors = Table.RemoveRowsWithErrors(Typed),
    Unique   = Table.Distinct(NoErrors, {"ProductID"})
in
    Unique
