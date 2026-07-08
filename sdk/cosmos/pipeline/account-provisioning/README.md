# Cosmos live-test account provisioning

Two scripts provision the fixed Cosmos DB accounts used by the **azure-sdk-for-java**
Cosmos live tests and publish the single secret the pipelines read
(`cosmos-live-test-accounts`):

- **`New-CosmosLiveTestAccounts.ps1`** — creates the resource group (if missing) + the
  accounts, and **outputs** the accounts JSON (endpoints + keys). No Key Vault access.
- **`Set-CosmosLiveTestAccountsSecret.ps1`** — takes that JSON and **writes it to Key
  Vault** as one secret. Kept separate so account creation and secret publishing have
  independent permissions/owners.

## Why

The Java Cosmos live tests are moving off the central EngSys on-the-fly provisioner to
**fixed, self-owned accounts** (no service-connection / tenant dependency for the
key-based tests). Because the ephemeral tenant is deleted and recreated roughly every
**90 days**, these scripts are re-run after each rotation to recreate the accounts and
publish the fresh endpoints/keys as one secret.

## Files

| File | Purpose |
| --- | --- |
| `New-CosmosLiveTestAccounts.ps1` | Creates `sdk-ci` RG (if missing) + accounts; outputs accounts JSON. |
| `Set-CosmosLiveTestAccountsSecret.ps1` | Publishes the accounts JSON to a Key Vault secret. |
| `cosmos-live-test-accounts.definition.json` | Desired accounts (logical selector + config). |

The emitted secret conforms to the schema at
`../live-test-accounts.schema.json`, which the pipeline pre-step
`../resolve-cosmos-test-account.sh` parses.

## Prerequisites

- PowerShell 7+
- Az modules: `Az.Accounts`, `Az.Resources`, `Az.CosmosDB` (account script);
  `Az.Accounts`, `Az.KeyVault` (secret script)
- Contributor on the subscription that hosts `sdk-ci` (account script)
- Set access to the target Key Vault secret — ensure **at least two** team members
  have set+get, not a single owner (secret script)
- Signed in: `Connect-AzAccount -Tenant <id> -Subscription <sub>`

## Usage

```powershell
# Create/refresh accounts and publish the secret in one pipeline
./New-CosmosLiveTestAccounts.ps1 -SubscriptionId <sub> |
    ./Set-CosmosLiveTestAccountsSecret.ps1 -KeyVaultName <kv>

# Or in two steps via a file (contains keys - treat as secret, delete after)
./New-CosmosLiveTestAccounts.ps1 -SubscriptionId <sub> -OutputPath ./accounts.json
./Set-CosmosLiveTestAccountsSecret.ps1 -KeyVaultName <kv> -InputPath ./accounts.json

# Dry run: create nothing, print the assembled JSON with keys stubbed
./New-CosmosLiveTestAccounts.ps1 -SubscriptionId <sub> -WhatIf
```

Both scripts are idempotent: existing accounts are left in place; the secret is
overwritten with current endpoints/keys.

## Verified run (2026-07-02)

Dry run validated end-to-end against `CosmosDB_Test_Subscription`
(`1b0c0c34-d01d-4293-8afe-768acc60f777`, tenant `97691679-...`): the account script
connects, enumerates the definition, and emits a schema-valid JSON (keys stubbed under
`-WhatIf`). The secret script's validation + `-WhatIf` publish path was tested against
the sample config. No resources were created. Account names are `sdkci-<selector>` (all
<=44 chars). Note: the `sdk-ci` resource group did not yet exist (the account script
creates it), and the target Key Vault must be confirmed (it may live in a different
subscription).

### Auth note (macOS / headless)

Az PowerShell `-AccessToken` bridging from the `az` CLI was rejected by Az.Accounts 3.x.
Use device-code login, and always pass `-Subscription` to avoid the interactive picker:

```powershell
Connect-AzAccount -UseDeviceAuthentication -Tenant <tenant> -Subscription <sub>
# subsequent runs reuse the cached context:
Connect-AzAccount -Tenant <tenant> -Subscription <sub>
```

## Rotation runbook

1. Ephemeral tenant is recreated (~every 90 days).
2. `Connect-AzAccount -Tenant <new-tenant-id> -Subscription <sub>`.
3. Run the account script piped into the secret script (above). It recreates any missing
   `sdk-ci` accounts and rewrites the `cosmos-live-test-accounts` secret.
4. Java pipelines pick up the refreshed values on their next run — no YAML edits.

Can be wired as a scheduled ADO pipeline that runs after the tenant-refresh job.
