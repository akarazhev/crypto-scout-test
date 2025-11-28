# Context: Implement a table and json model for `BTC-USD` 1W data

In this `crypto-scout-test` project let's implement an `SQL` table and `json` model for `BTC-USD` 1W data. The related
tests should also be updated.

## Roles

Take the following roles:

- Expert java engineer.
- Expert database engineer.

## Conditions

- Change current implementation: @cmc_parser_tables.sql, @AssertCmcParserTablesTest.java, @MockCmcParserTablesTest.java.
- Add implementation in: @kline.W.json.
- Rely on the section: `BTC-USD` 1W data model in json.
- Double-check your proposal and make sure that they are correct and haven't missed any important points.
- Implementation must be production ready.
- Use the best practices and design patterns.

## Constraints

- Use the current technological stack, that's: `Java 25`, `ActiveJ 6.0-rc2`.
- Follow the current code style.
- Do not hallucinate.

## Tasks

- As the `expert database engineer` implement the `cmc_kline_1w` sql table in @cmc_parser_tables.sql taking into account that one
  row represents the data for the day, so it might be `365` rows per year. Adjust indexes, compressions and policies.
- As the `expert database engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.
- As the `expert java engineer` implement the `1w` data in @kline.W.json taking into account that it receives one row
  per request. Update the implementations @AssertCmcParserTablesTest.java, @MockCmcParserTablesTest.java to test changes.
- As the `expert java engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.

## `BTC-USD` 1W data model in json

```json
{
  "id": 1,
  "name": "Bitcoin",
  "symbol": "BTC",
  "timeEnd": "1522627199",
  "quotes": [
    {
      "timeOpen": "2025-11-17T00:00:00.000Z",
      "timeClose": "2025-11-23T23:59:59.999Z",
      "timeHigh": "2025-11-17T08:26:00.000Z",
      "timeLow": "2025-11-21T12:30:00.000Z",
      "quote": {
        "name": "2781",
        "open": 94180.8763285011,
        "high": 95928.3695793783,
        "low": 80659.8124264662,
        "close": 86805.0080755520,
        "volume": 659958211589.4600000000,
        "marketCap": 1731864376001.4300000000,
        "circulatingSupply": 19952637,
        "timestamp": "2025-11-23T23:59:59.999Z"
      }
    }
  ]
}
```