// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "raw\sales_india_north.csv"), [Delimiter = ",", Columns = 9, Encoding = 65001, QuoteStyle = QuoteStyle.Csv]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Typed    = Table.TransformColumnTypes(Promoted, {
                 {"OrderID", type text}, {"OrderDate", type date}, {"StoreCode", type text}, {"CustomerID", type text},
                 {"ProductID", type text}, {"Qty", Int64.Type}, {"UnitPrice", type number}, {"DiscountPct", type number},
                 {"Channel", type text}}, "en-US"),
    Renamed  = Table.RenameColumns(Typed, {{"UnitPrice", "UnitPriceINR"}}),
    Source_  = Table.AddColumn(Renamed, "SourceFile", each "sales_india_north.csv", type text)
in
    Source_
