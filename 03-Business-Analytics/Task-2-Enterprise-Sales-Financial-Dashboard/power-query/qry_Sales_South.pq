// Sumerix Global · Business Analytics Task 2 · Eswar Mahalingam
// Paste into Excel: Data ▸ Get Data ▸ From Other Sources ▸ Blank Query ▸ Advanced Editor. Query name = file name.
let
    Source   = Excel.Workbook(File.Contents(DataFolder & "raw\sales_india_south.xlsx"), null, true),
    Sheet    = Source{[Item = "Sales", Kind = "Sheet"]}[Data],
    Promoted = Table.PromoteHeaders(Sheet, [PromoteAllScalars = true]),
    Renamed  = Table.RenameColumns(Promoted, {
                 {"Invoice No", "OrderID"}, {"Invoice Date", "OrderDate"}, {"Store", "StoreCode"}, {"Cust", "CustomerID"},
                 {"SKU", "ProductID"}, {"Units", "Qty"}, {"Rate", "UnitPriceINR"}, {"Disc %", "DiscountPct"}, {"Mode", "Channel"}}),
    // dates arrive as dd-mm-yyyy text
    Dates    = Table.TransformColumns(Renamed, {{"OrderDate", each Date.FromText(Text.From(_), [Format = "dd-MM-yyyy", Culture = "en-GB"]), type date}}),
    // discount arrives as whole numbers (5 = 5%)
    Discount = Table.TransformColumns(Dates, {{"DiscountPct", each Number.From(_) / 100, type number}}),
    Typed    = Table.TransformColumnTypes(Discount, {
                 {"OrderID", type text}, {"StoreCode", type text}, {"CustomerID", type text}, {"ProductID", type text},
                 {"Qty", Int64.Type}, {"UnitPriceINR", type number}, {"Channel", type text}}),
    Source_  = Table.AddColumn(Typed, "SourceFile", each "sales_india_south.xlsx", type text)
in
    Source_
