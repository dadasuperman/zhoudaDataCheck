# Full rename: datavines / io.datavines / DataVines / yihongMeta(brand) -> zhoudaDataCheck stack
# Preserves: yihong_meta (DB name)
$ErrorActionPreference = 'Stop'
$Root = Resolve-Path (Join-Path $PSScriptRoot '..')
Write-Host "Root: $Root"

$ExcludeDirPattern = '\\(target|node_modules|\.git|\.idea|dist|logs|agent-tools)\\'

function Test-Excluded([string]$path) {
    return $path -match $ExcludeDirPattern
}

$TextExt = @(
    '.java','.xml','.yml','.yaml','.properties','.sql','.sh','.bat','.cmd','.md','.txt',
    '.json','.ts','.tsx','.js','.jsx','.less','.css','.html','.htm','.gradle','.factories',
    '.gitignore','.gitattributes','.editorconfig','.iml','.lst','.conf','.ini','.service'
)

Write-Host "=== Phase 1: content replace ==="
$files = Get-ChildItem -Path $Root -Recurse -File -ErrorAction SilentlyContinue | Where-Object {
    -not (Test-Excluded $_.FullName) -and (
        ($TextExt -contains $_.Extension.ToLower()) -or
        $_.Name -match '^(Dockerfile|HEADER|LICENSE|NOTICE|mvnw)$' -or
        $_.FullName -match 'META-INF\\services\\'
    )
}

$replaced = 0
foreach ($f in $files) {
    try {
        $raw = [System.IO.File]::ReadAllText($f.FullName)
    } catch {
        continue
    }
    $new = $raw
    # Package / FQCN first
    $new = $new -replace 'io\.datavines','io.zhoudadatacheck'
    # Class / type identifiers (order: longer / specific first)
    $new = $new -replace 'DataVines','ZhoudaDataCheck'
    $new = $new -replace 'Datavines','ZhoudaDataCheck'
    $new = $new -replace 'DATAVINES','ZHOUDA_DATA_CHECK'
    # Brand display (keep yihong_meta)
    $new = $new -replace 'yihongMeta','zhoudaDataCheck'
    # Maven / path tokens: longest module-ish prefixes via generic datavines-
    $new = $new -replace 'datavines-','zhouda-data-check-'
    $new = $new -replace 'datavines/','zhouda-data-check/'
    $new = $new -replace 'datavines\\','zhouda-data-check\'
    # Remaining bare "datavines" as word (artifact root, docs) — avoid touching already replaced
    $new = $new -creplace '(?<![A-Za-z0-9_-])datavines(?![A-Za-z0-9_-])','zhouda-data-check'
    $new = $new -creplace '(?<![A-Za-z0-9_-])DataVines(?![A-Za-z0-9_-])','ZhoudaDataCheck'
    # groupId leftover
    $new = $new -replace '<groupId>io\.zhouda-data-check</groupId>','<groupId>io.zhoudadatacheck</groupId>'

    if ($new -ne $raw) {
        [System.IO.File]::WriteAllText($f.FullName, $new)
        $replaced++
    }
}
Write-Host "Content files updated: $replaced"

Write-Host "=== Phase 2: rename SPI service files ==="
$spiFiles = Get-ChildItem -Path $Root -Recurse -File -ErrorAction SilentlyContinue | Where-Object {
    -not (Test-Excluded $_.FullName) -and
    $_.DirectoryName -match 'META-INF\\services$' -and
    $_.Name -like 'io.datavines*'
}
foreach ($s in $spiFiles) {
    $newName = $s.Name -replace '^io\.datavines','io.zhoudadatacheck'
    $dest = Join-Path $s.DirectoryName $newName
    if ($dest -ne $s.FullName) {
        Move-Item -LiteralPath $s.FullName -Destination $dest -Force
        Write-Host "SPI: $($s.Name) -> $newName"
    }
}
# Also fix any that were content-replaced filename still wrong if already renamed path
$spiFiles2 = Get-ChildItem -Path $Root -Recurse -File -ErrorAction SilentlyContinue | Where-Object {
    -not (Test-Excluded $_.FullName) -and
    $_.DirectoryName -match 'META-INF\\services$' -and
    $_.Name -like 'io.zhouda-data-check*'
}
foreach ($s in $spiFiles2) {
    # accidental if package path used hyphen — should not happen for SPI names
}

Write-Host "=== Phase 3: rename Java/source files with DataVines/Datavines in name ==="
$javaNamed = Get-ChildItem -Path $Root -Recurse -File -ErrorAction SilentlyContinue | Where-Object {
    -not (Test-Excluded $_.FullName) -and ($_.Name -match 'DataVines|Datavines|datavines')
}
foreach ($j in $javaNamed) {
    $nn = $j.Name
    $nn = $nn -replace 'DataVines','ZhoudaDataCheck'
    $nn = $nn -replace 'Datavines','ZhoudaDataCheck'
    $nn = $nn -replace 'datavines','zhouda-data-check'
    if ($nn -ne $j.Name) {
        $dest = Join-Path $j.DirectoryName $nn
        if (-not (Test-Path $dest)) {
            Rename-Item -LiteralPath $j.FullName -NewName $nn
            Write-Host "File: $($j.Name) -> $nn"
        }
    }
}

Write-Host "=== Phase 4: move package dirs io/datavines -> io/zhoudadatacheck ==="
# Find all .../java/io/datavines and .../resources that have only package move for java
$datavinesDirs = Get-ChildItem -Path $Root -Recurse -Directory -ErrorAction SilentlyContinue | Where-Object {
    -not (Test-Excluded $_.FullName) -and $_.Name -eq 'datavines' -and $_.Parent.Name -eq 'io'
}
# deepest first not needed for sibling package moves; process each
foreach ($d in ($datavinesDirs | Sort-Object { $_.FullName.Length } -Descending)) {
    $dest = Join-Path $d.Parent.FullName 'zhoudadatacheck'
    if (Test-Path $dest) {
        # merge
        Get-ChildItem -LiteralPath $d.FullName -Force | ForEach-Object {
            $target = Join-Path $dest $_.Name
            if (Test-Path $target) {
                if ($_.PSIsContainer) {
                    Write-Host "WARN merge dir exists: $target"
                } else {
                    Move-Item -LiteralPath $_.FullName -Destination $target -Force
                }
            } else {
                Move-Item -LiteralPath $_.FullName -Destination $dest -Force
            }
        }
        Remove-Item -LiteralPath $d.FullName -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "Merged package: $($d.FullName)"
    } else {
        Rename-Item -LiteralPath $d.FullName -NewName 'zhoudadatacheck'
        Write-Host "Package dir: $($d.FullName) -> zhoudadatacheck"
    }
}

Write-Host "=== Phase 5: rename module directories (deepest first) ==="
$moduleDirs = Get-ChildItem -Path $Root -Recurse -Directory -ErrorAction SilentlyContinue | Where-Object {
    -not (Test-Excluded $_.FullName) -and $_.Name -like 'datavines-*'
} | Sort-Object { $_.FullName.Length } -Descending

foreach ($d in $moduleDirs) {
    $nn = $d.Name -replace '^datavines-','zhouda-data-check-'
    $dest = Join-Path $d.Parent.FullName $nn
    if (Test-Path $dest) {
        Write-Host "SKIP exists: $dest"
        continue
    }
    Rename-Item -LiteralPath $d.FullName -NewName $nn
    Write-Host "Module: $($d.Name) -> $nn"
}

# Root-level leftover scripts
$binFiles = Get-ChildItem -Path (Join-Path $Root 'bin') -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -like 'datavines-*' -or $_.Name -like 'zhouda-data-check-*' }
# already renamed by phase 3 if matched

# SQL scripts under scripts/
$sqlFiles = Get-ChildItem -Path (Join-Path $Root 'scripts') -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -match 'datavines' }
foreach ($s in $sqlFiles) {
    $nn = $s.Name -replace 'datavines','zhouda-data-check'
    if ($nn -ne $s.Name) {
        Rename-Item -LiteralPath $s.FullName -NewName $nn -ErrorAction SilentlyContinue
        Write-Host "Script: $($s.Name) -> $nn"
    }
}

# deploy k8s/docker filenames
$deployFiles = Get-ChildItem -Path (Join-Path $Root 'deploy') -Recurse -File -ErrorAction SilentlyContinue | Where-Object { $_.Name -match 'datavines' }
foreach ($s in $deployFiles) {
    $nn = $s.Name -replace 'datavines','zhouda-data-check'
    if ($nn -ne $s.Name) {
        Rename-Item -LiteralPath $s.FullName -NewName $nn -ErrorAction SilentlyContinue
        Write-Host "Deploy: $($s.Name) -> $nn"
    }
}

Write-Host "=== Phase 6: fix groupId if hyphenated incorrectly ==="
$poms = Get-ChildItem -Path $Root -Recurse -Filter 'pom.xml' -File -ErrorAction SilentlyContinue | Where-Object { -not (Test-Excluded $_.FullName) }
foreach ($p in $poms) {
    $c = [System.IO.File]::ReadAllText($p.FullName)
    $n = $c -replace 'io\.zhouda-data-check','io.zhoudadatacheck'
    # ensure groupId
    $n = $n -replace '<groupId>io\.datavines</groupId>','<groupId>io.zhoudadatacheck</groupId>'
    if ($n -ne $c) {
        [System.IO.File]::WriteAllText($p.FullName, $n)
    }
}

Write-Host "=== DONE ==="
# Quick sanity
$leftPkg = (Select-String -Path (Get-ChildItem $Root -Recurse -Include *.java -File | Where-Object { -not (Test-Excluded $_.FullName) } | Select-Object -ExpandProperty FullName) -Pattern 'io\.datavines' -SimpleMatch:$false -ErrorAction SilentlyContinue | Measure-Object).Count
Write-Host "Remaining io.datavines in java (approx matches): check manually"
$leftDirs = Get-ChildItem $Root -Directory -Filter 'datavines-*' -ErrorAction SilentlyContinue
Write-Host "Remaining root datavines-* dirs: $($leftDirs.Count)"
