// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "reference\stores.csv"), [Delimiter = ",", Encoding = 65001, QuoteStyle = QuoteStyle.Csv]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Trimmed  = Table.TransformColumns(Promoted, {}, Text.Trim),
    Typed    = Table.TransformColumnTypes(Trimmed, {{"StoreCode", type text}, {"StoreName", type text}, {"City", type text}, {"Region", type text}, {"Country", type text}, {"Currency", type text}}, "en-US"),
    NoErrors = Table.RemoveRowsWithErrors(Typed),
    Unique   = Table.Distinct(NoErrors, {"StoreCode"})
in
    Unique
