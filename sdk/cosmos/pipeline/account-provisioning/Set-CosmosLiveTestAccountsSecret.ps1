<#
.SYNOPSIS
    Publishes the cosmos-live-test-accounts JSON to a Key Vault secret.

.DESCRIPTION
    Companion to New-CosmosLiveTestAccounts.ps1. Takes the assembled accounts JSON
    (endpoints + keys), validates it, and writes it to Key Vault as a single secret that
    the azure-sdk-for-java Cosmos pipelines read (via resolve-cosmos-test-account.sh).

    The JSON can be supplied on the pipeline (piped from New-CosmosLiveTestAccounts.ps1),
    via -InputPath, or via -Json.

    Uses the Az PowerShell modules (Az.Accounts, Az.KeyVault).

.PARAMETER KeyVaultName
    Key Vault that stores the single secret.

.PARAMETER SecretName
    Secret name. Defaults to 'cosmos-live-test-accounts'.

.PARAMETER InputPath
    Path to a file containing the accounts JSON. Mutually exclusive with -Json / pipeline.

.PARAMETER Json
    The accounts JSON as a string. Also accepts pipeline input.

.EXAMPLE
    ./New-CosmosLiveTestAccounts.ps1 -SubscriptionId <sub> |
        ./Set-CosmosLiveTestAccountsSecret.ps1 -KeyVaultName <kv>

.EXAMPLE
    ./Set-CosmosLiveTestAccountsSecret.ps1 -KeyVaultName <kv> -InputPath ./accounts.json

.NOTES
    Requires: PowerShell 7+, Az modules, and set access to the Key Vault secret
    (ensure at least two team members hold set+get on this secret).
#>
[CmdletBinding(SupportsShouldProcess = $true)]
param(
    [Parameter(Mandatory = $true)]
    [string] $KeyVaultName,

    [string] $SecretName = 'cosmos-live-test-accounts',

    [string] $InputPath,

    [Parameter(ValueFromPipeline = $true)]
    [string] $Json
)

begin {
    $ErrorActionPreference = 'Stop'
    Set-StrictMode -Version Latest
    function Write-Info($msg) { Write-Host "==> $msg" -ForegroundColor Cyan }

    foreach ($m in @('Az.Accounts', 'Az.KeyVault')) {
        if (-not (Get-Module -ListAvailable -Name $m)) {
            throw "Required module '$m' is not installed. Install with: Install-Module $m -Scope CurrentUser"
        }
    }
    $pipedChunks = [System.Collections.Generic.List[string]]::new()
}

process {
    if ($Json) { $pipedChunks.Add($Json) }
}

end {
    # Resolve the JSON source: -InputPath, then -Json/pipeline.
    if ($InputPath) {
        if ($pipedChunks.Count -gt 0) { throw "Provide either -InputPath or -Json/pipeline input, not both." }
        if (-not (Test-Path $InputPath)) { throw "Input file not found: $InputPath" }
        $payload = Get-Content -Raw -Path $InputPath
    } elseif ($pipedChunks.Count -gt 0) {
        $payload = ($pipedChunks -join "`n")
    } else {
        throw "No accounts JSON supplied. Use -InputPath, -Json, or pipe from New-CosmosLiveTestAccounts.ps1."
    }

    # Validate before writing.
    try {
        $parsed = $payload | ConvertFrom-Json
    } catch {
        throw "Supplied content is not valid JSON: $($_.Exception.Message)"
    }
    $ver = if ($parsed.PSObject.Properties.Name -contains 'version') { $parsed.version } else { $null }
    if ($ver -ne 1) {
        throw "Accounts JSON must have version = 1 (got '$ver')."
    }
    if (-not ($parsed.PSObject.Properties.Name -contains 'accounts') -or @($parsed.accounts.PSObject.Properties).Count -eq 0) {
        throw "Accounts JSON must contain a non-empty 'accounts' object."
    }
    foreach ($p in $parsed.accounts.PSObject.Properties) {
        $names = $p.Value.PSObject.Properties.Name
        $hasEndpoint = ($names -contains 'endpoint') -and $p.Value.endpoint
        $hasKey = ($names -contains 'key') -and $p.Value.key
        if (-not $hasEndpoint -or -not $hasKey) {
            throw "Account '$($p.Name)' is missing required 'endpoint' or 'key'."
        }
    }
    $count = @($parsed.accounts.PSObject.Properties).Count

    if ($PSCmdlet.ShouldProcess("$KeyVaultName/$SecretName", "Set Key Vault secret ($count accounts)")) {
        Write-Info "Writing secret '$SecretName' to Key Vault '$KeyVaultName' ($count accounts)"
        $secure = ConvertTo-SecureString -String $payload -AsPlainText -Force
        $null = Set-AzKeyVaultSecret -VaultName $KeyVaultName -Name $SecretName -SecretValue $secure
        Write-Info "Done."
    }
}
