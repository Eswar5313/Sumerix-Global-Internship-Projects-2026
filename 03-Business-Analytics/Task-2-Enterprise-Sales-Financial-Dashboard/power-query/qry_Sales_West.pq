// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "raw\sales_india_west.csv"), [Delimiter = ";", Columns = 9, Encoding = 65001, QuoteStyle = QuoteStyle.Csv]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Renamed  = Table.RenameColumns(Promoted, {
                 {"order_id", "OrderID"}, {"date", "OrderDate"}, {"store", "StoreCode"}, {"customer", "CustomerID"},
                 {"product", "ProductID"}, {"quantity", "Qty"}, {"price", "UnitPriceINR"}, {"discount", "DiscountPct"}, {"channel", "Channel"}}),
    // dd/mm/yyyy → date using the en-GB locale
    Dates    = Table.TransformColumnTypes(Renamed, {{"OrderDate", type date}}, "en-GB"),
    Typed    = Table.TransformColumnTypes(Dates, {
                 {"OrderID", type text}, {"StoreCode", type text}, {"CustomerID", type text}, {"ProductID", type text},
                 {"Qty", Int64.Type}, {"UnitPriceINR", type number}, {"DiscountPct", type number}, {"Channel", type text}}, "en-US"),
    // STORE / ONLINE → Store / Online; blank channel → Unknown
    Proper   = Table.TransformColumns(Typed, {{"Channel", Text.Proper, type text}}),
    Blanks   = Table.ReplaceValue(Proper, "", "Unknown", Replacer.ReplaceValue, {"Channel"}),
    Deduped  = Table.Distinct(Blanks),
    Source_  = Table.AddColumn(Deduped, "SourceFile", each "sales_india_west.csv", type text)
in
    Source_
