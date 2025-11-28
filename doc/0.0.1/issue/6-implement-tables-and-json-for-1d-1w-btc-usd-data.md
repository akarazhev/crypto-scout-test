# Context: Implement a table and json model for `BTC-USD` 1D data

In this `crypto-scout-test` project let's implement an `SQL` table and `json` model for `BTC-USD` 1D data. The related
tests should also be updated.

## Roles

Take the following roles:

- Expert java engineer.
- Expert database engineer.

## Conditions

- Change current implementation: @cmc_parser_tables.sql, @AssertCmcParserTablesTest.java, @MockCmcParserTablesTest.java.
- Add implementation in: @kline.D.json.
- Rely on the section: `BTC-USD` 1D data model in json.
- Double-check your proposal and make sure that they are correct and haven't missed any important points.
- Implementation must be production ready.
- Use the best practices and design patterns.

## Constraints

- Use the current technological stack, that's: `Java 25`, `ActiveJ 6.0-rc2`.
- Follow the current code style.
- Do not hallucinate.

## Tasks

- As the `expert database engineer` implement the `cmc_kline_1d` sql table in @cmc_parser_tables.sql taking into account that one
  row represents the data for the day, so it might be `365` rows per year. Adjust indexes, compressions and policies.
- As the `expert database engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.
- As the `expert java engineer` implement the `1d` data in @kline_1d.json taking into account that it receives one row
  per request. Update the implementations @AssertCmcParserTablesTest.java, @MockCmcParserTablesTest.java to test changes.
- As the `expert database engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.

## `BTC-USD` 1D data model in json

```json
{
  "id": 1,
  "name": "Bitcoin",
  "symbol": "BTC",
  "timeEnd": "1729900799",
  "quotes": [
    {
      "timeOpen": "2025-11-27T00:00:00.000Z",
      "timeClose": "2025-11-27T23:59:59.999Z",
      "timeHigh": "2025-11-27T09:38:00.000Z",
      "timeLow": "2025-11-27T00:16:00.000Z",
      "quote": {
        "name": "2781",
        "open": 90517.7657112842,
        "high": 91897.5753373039,
        "low": 90089.5162623831,
        "close": 91285.3723441706,
        "volume": 57040622844.6700000000,
        "marketCap": 1821561772280.6200000000,
        "circulatingSupply": 19954584,
        "timestamp": "2025-11-27T23:59:59.999Z"
      }
    }
  ]
}
```