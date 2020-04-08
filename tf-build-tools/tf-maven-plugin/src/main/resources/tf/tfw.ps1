#Set-PSDebug -Trace 1

##############################################################
# try to figure out where the root directory of the project is
##############################################################
$arg_list       = $args
if ($arg_list -eq "") {
    $arg_list   = " "
}
$cur_dir        = Get-Location
$target_curdir  = "$cur_dir" + "\.tf"
$projectBaseDir = ""

While ( $projectBaseDir -eq ""){
    if (Test-Path -Path "$target_curdir") {
        $projectBaseDir = "$cur_dir"
    } Else {
        cd ..
        $cur_dir        = Get-Location
        $target_curdir = "$cur_dir" + "\.tf"
    }
}

$targetDir = "$projectBaseDir" + "\.tf\"
$propFile  = "$targetDir"      + "terraform-maven.properties"

################################################################
# read all lines from the properties file and store in variables
################################################################
Get-Content $propFile | ForEach-Object {
    if($_ -match "distributionSite"){
        $pos              = $_.IndexOf("=")
        $distributionSite = $_.Substring($pos+1)
    }
    if($_ -match "releaseDir"      ){
        $pos              = $_.IndexOf("=")
        $releaseDir       = $_.Substring($pos+1)
    }
    if($_ -match "releaseName"     ){
        $pos              = $_.IndexOf("=")
        $releaseName      = $_.Substring($pos+1)
    }
    if($_ -match "releaseVer"      ){
        $pos              = $_.IndexOf("=")
        $releaseVer       = $_.Substring($pos+1)
    }
    if($_ -match "releaseOS"       ){
        $pos              = $_.IndexOf("=")
        $releaseOS        = $_.Substring($pos+1)
    }
    if($_ -match "releaseSuffix"   ){
        $pos              = $_.IndexOf("=")
        $releaseSuffix    = $_.Substring($pos+1)
    }
}

#################################################################################
# Construct the full zip file name, full binary file name, and URL of the package
#################################################################################
$terraformZip = "$targetDir" +
              "$releaseName" +
              "_"            +
              "$releaseVer"  +
              "_"            +
              "$releaseOS"   +
              "_"            +
              "$releaseSuffix"

$terraformBinary = "$targetDir" +
                   "terraform.exe"

$releaseSource = "$distributionSite" +
        "/"                          +
        "$releaseDir"                +
        "/"                          +
        "$releaseVer"                +
        "/"                          +
        "$releaseName"               +
        "_"                          +
        "$releaseVer"                +
        "_"                          +
        "$releaseOS"                 +
        "_"                          +
        "$releaseSuffix"

##########################################################################
# if the terraform binary is already there, figure out what version it is
##########################################################################
if ( [System.IO.File]::Exists($terraformBinary) ) {
    $versionString       = & $terraformBinary -version
    $arr                 = $versionString.split(" ")
    $installedVerionFull = $arr[1]
    $len                 = $installedVerionFull.Length
    $installedVerion     = $installedVerionFull.substring(1,$len-1)

    ###############################################################
    # if the terraform binary is already there, check if it is the
    # same version as the properties file indicates
    # if not, download and install the desired version
    ###############################################################
    if ($installedVerion -eq $releaseVer) {
        Write-Host ""
    } else {
        Write-Host "Different version, downloading"
        try {
            $WebClient = New-Object System.Net.WebClient;
            $webclient.DownloadFile($releaseSource, $terraformZip)
        }
        catch {
            "An error occurred that could not be resolved."
        }
        & del $terraformBinary
        try {
            Expand-Archive -LiteralPath $terraformZip -DestinationPath $targetDir
        }
        catch {
            "An error occurred that could not be resolved."
        }
        & del $terraformZip
    }
} else {
    ############################################
    # if the terraform binary is not there,
    # download and install the desired version
    ############################################
    Write-Host "Terraform not found, installing"
    try {
        $WebClient = New-Object System.Net.WebClient;
        $webclient.DownloadFile($releaseSource, $terraformZip)
    }
    catch {
        "An error occurred that could not be resolved."
    }
    if ( [System.IO.File]::Exists($terraformBinary) ) {
        & del $terraformBinary
    }
    try {
        Expand-Archive -LiteralPath $terraformZip -DestinationPath $targetDir
    }
    catch {
        "An error occurred that could not be resolved."
    }
    & del $terraformZip
}
###################################################
# Run terraform with the supplied arguments if any
###################################################
try {
        Invoke-Expression "$terraformBinary $arg_list"
}
catch {
    "An error occurred invoking terraform.exe."
}
exit
