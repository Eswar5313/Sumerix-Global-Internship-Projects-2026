// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "reference\fx_rates.csv"), [Delimiter = ",", Encoding = 65001]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Typed    = Table.TransformColumnTypes(Promoted, {{"Year", Int64.Type}, {"FromCcy", type text}, {"ToCcy", type text}, {"Rate", type number}}, "en-US"),
    AEDOnly  = Table.SelectRows(Typed, each [FromCcy] = "AED" and [ToCcy] = "INR")
in
    AEDOnly
