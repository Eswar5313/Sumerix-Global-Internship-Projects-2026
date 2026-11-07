// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Csv.Document(File.Contents(DataFolder & "raw\sales_uae.csv"), [Delimiter = ",", Columns = 9, Encoding = 65001, QuoteStyle = QuoteStyle.Csv]),
    Promoted = Table.PromoteHeaders(Source, [PromoteAllScalars = true]),
    Renamed  = Table.RenameColumns(Promoted, {
                 {"OrderRef", "OrderID"}, {"TxnDate", "OrderDate"}, {"Branch", "StoreCode"}, {"Customer", "CustomerID"},
                 {"Item", "ProductID"}, {"SalesChannel", "Channel"}}),
    Typed    = Table.TransformColumnTypes(Renamed, {
                 {"OrderID", type text}, {"OrderDate", type date}, {"StoreCode", type text}, {"CustomerID", type text},
                 {"ProductID", type text}, {"Qty", Int64.Type}, {"PriceAED", type number}, {"DiscountPct", type number},
                 {"Channel", type text}}, "en-US"),
    AddYear  = Table.AddColumn(Typed, "Year", each Date.Year([OrderDate]), Int64.Type),
    MergeFX  = Table.NestedJoin(AddYear, {"Year"}, qry_FX, {"Year"}, "FX", JoinKind.LeftOuter),
    ExpandFX = Table.ExpandTableColumn(MergeFX, "FX", {"Rate"}, {"Rate"}),
    ToINR    = Table.AddColumn(ExpandFX, "UnitPriceINR", each Number.Round([PriceAED] * [Rate], 2, RoundingMode.ToEven), type number),
    Removed  = Table.RemoveColumns(ToINR, {"PriceAED", "Rate", "Year"}),
    Ordered  = Table.ReorderColumns(Removed, {"OrderID", "OrderDate", "StoreCode", "CustomerID", "ProductID", "Qty", "UnitPriceINR", "DiscountPct", "Channel"}),
    Source_  = Table.AddColumn(Ordered, "SourceFile", each "sales_uae.csv", type text)
in
    Source_
