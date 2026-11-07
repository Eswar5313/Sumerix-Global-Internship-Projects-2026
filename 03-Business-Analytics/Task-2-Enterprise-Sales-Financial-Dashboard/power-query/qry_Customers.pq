// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "reference\customers.csv"), [Delimiter = ",", Encoding = 65001, QuoteStyle = QuoteStyle.Csv]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Trimmed  = Table.TransformColumns(Promoted, {}, Text.Trim),
    Typed    = Table.TransformColumnTypes(Trimmed, {{"CustomerID", type text}, {"CustomerName", type text}, {"Segment", type text}, {"HomeCity", type text}, {"Country", type text}, {"FirstPurchase", type date}}, "en-US"),
    NoErrors = Table.RemoveRowsWithErrors(Typed),
    Unique   = Table.Distinct(NoErrors, {"CustomerID"})
in
    Unique
