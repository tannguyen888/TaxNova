[Reflection.Assembly]::LoadWithPartialName('System.Security') | Out-Null
$bytes = [Text.Encoding]::UTF8.GetBytes('admin123')
$sha256 = [Security.Cryptography.SHA256]::Create()
$hash = [Convert]::ToBase64String($sha256.ComputeHash($bytes))
Write-Host "Password hash: $hash"
