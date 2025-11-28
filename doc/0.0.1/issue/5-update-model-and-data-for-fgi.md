# Context: Update data model and test data of the `FGI` that comes from `CoinMarketCap`

In this `crypto-scout-test` project let's update the `SQL` data model and `json` test data of the `FGI` that comes
from `CoinMarketCap`. The related tests should also be updated. `FGI` stands for `Fear & Greed Index`.

## Roles

Take the following roles:

- Expert java engineer.
- Expert database engineer.

## Conditions

- Change current implementations: @cmc_parser_tables.sql, @cmc_fgi.json, @AssertCmcParserTablesTest.java,
  @MockCmcParserTablesTest.java.
- Rely on the section: `FGI` data model in json.
- Double-check your proposal and make sure that they are correct and haven't missed any important points.
- Implementation must be production ready.
- Use the best practices and design patterns.

## Constraints

- Use the current technological stack, that's: `Java 25`, `ActiveJ 6.0-rc2`.
- Follow the current code style.
- Do not hallucinate.

## Tasks

- As the `expert database engineer` update the `FGI` sql table in @cmc_parser_tables.sql taking into account that one
  row represents the data for the day, so it might be `365` rows per year. Adjust indexes and compressions.
- As the `expert database engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.
- As the `expert java engineer` update the `FGI` json data in @cmc_fgi.json taking into account that it receives one row
  per request.
- As the `expert database engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.
- As the `expert java engineer` update the implementations @AssertCmcParserTablesTest.java,
  @MockCmcParserTablesTest.java to test changes.
- As the `expert database engineer` double-check your proposal and make sure that they are correct and haven't missed
  any important points.

## `FGI` data model in json

```json
{
  "value": 20,
  "update_time": "2025-11-28T12:38:10.026Z",
  "value_classification": "Fear"
}
```