// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Appended   = Table.Combine({qry_Sales_North, qry_Sales_South, qry_Sales_West, qry_Sales_UAE}),
    MStore     = Table.NestedJoin(Appended, {"StoreCode"}, qry_Stores, {"StoreCode"}, "S", JoinKind.LeftOuter),
    XStore     = Table.ExpandTableColumn(MStore, "S", {"Region", "Country"}),
    MProduct   = Table.NestedJoin(XStore, {"ProductID"}, qry_Products, {"ProductID"}, "P", JoinKind.LeftOuter),
    XProduct   = Table.ExpandTableColumn(MProduct, "P", {"Category", "UnitCostINR"}),
    MCustomer  = Table.NestedJoin(XProduct, {"CustomerID"}, qry_Customers, {"CustomerID"}, "C", JoinKind.LeftOuter),
    XCustomer  = Table.ExpandTableColumn(MCustomer, "C", {"Segment"}),
    Ordered    = Table.ReorderColumns(XCustomer, {"OrderID", "OrderDate", "StoreCode", "Region", "Country", "CustomerID", "Segment",
                   "ProductID", "Category", "Channel", "Qty", "UnitPriceINR", "DiscountPct", "UnitCostINR", "SourceFile"}),
    Sorted     = Table.Sort(Ordered, {{"OrderDate", Order.Ascending}, {"OrderID", Order.Ascending}}),
    // the workbook keeps these as live worksheet formulas (columns P–W); Power BI users can keep them here instead
    Gross      = Table.AddColumn(Sorted, "GrossSales", each [Qty] * [UnitPriceINR], type number),
    DiscAmt    = Table.AddColumn(Gross, "DiscountAmt", each [GrossSales] * [DiscountPct], type number),
    NetRev     = Table.AddColumn(DiscAmt, "NetRevenue", each [GrossSales] - [DiscountAmt], type number),
    COGS       = Table.AddColumn(NetRev, "COGS", each [Qty] * [UnitCostINR], type number),
    GP         = Table.AddColumn(COGS, "GrossProfit", each [NetRevenue] - [COGS], type number),
    Unmatched  = Table.SelectRows(GP, each [Region] = null or [Category] = null or [Segment] = null),
    // data-quality guard: fail the refresh loudly if any key did not match a dimension
    Result     = if Table.RowCount(Unmatched) = 0 then GP else error Error.Record("Unmatched keys", Text.From(Table.RowCount(Unmatched)) & " rows have no dimension match")
in
    Result
