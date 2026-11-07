# Power Query (M) scripts

| Order | Query | Source | Load to |
|---|---|---|---|
| 1 | `DataFolder` | parameter | Connection only |
| 2 | `qry_Sales_North` | data/raw or data/reference | Connection only |
| 3 | `qry_Sales_South` | data/raw or data/reference | Connection only |
| 4 | `qry_Sales_West` | data/raw or data/reference | Connection only |
| 5 | `qry_FX` | data/raw or data/reference | Connection only |
| 6 | `qry_Sales_UAE` | data/raw or data/reference | Connection only |
| 7 | `qry_Stores` | data/raw or data/reference | Table + Data Model |
| 8 | `qry_Products` | data/raw or data/reference | Table + Data Model |
| 9 | `qry_Customers` | data/raw or data/reference | Table + Data Model |
| 10 | `qry_Budget` | data/raw or data/reference | Table + Data Model |
| 11 | `qry_Calendar` | generated | Table + Data Model |
| 12 | `qry_Sales_All` | append + merges | Table + Data Model |

## How to load
1. Excel ▸ Data ▸ Get Data ▸ From Other Sources ▸ **Blank Query** ▸ Advanced Editor → paste `DataFolder.pq`, rename the query **DataFolder**, and set the path to your local `data\` folder.
2. Repeat for every file in the order above (the query name must equal the file name, because later queries reference earlier ones).
3. Close & Load To… → *Only Create Connection* for the four branch queries and `qry_FX`; *Table + Add to Data Model* for the rest.
4. Data ▸ **Refresh All** rebuilds the whole model when a new month's extracts are dropped into `data\raw\`.

The same steps were reproduced in Python (`etl()` in the generator) to verify row counts: North 1,756 · South 2,053 · West 1,420 (after removing 2 duplicate lines; 3 blank channels → *Unknown*) · UAE 1,610 → **6,839** fact rows. `qry_Sales_All` raises a refresh error if any key fails to match a dimension.
