#Set-PSDebug -Trace 2

$arg_list       = $args
if ($arg_list -eq "") {
    $arg_list   = " "
}

function downloadTerraformBinary($terraformBinarySource, $targetDir)
{
    $terraformZip = $targetDir + "terraform.zip"
    try {
        $WebClient = New-Object System.Net.WebClient;
        $webclient.DownloadFile($terraformBinarySource, $terraformZip)
    }
    catch {
        "An error occurred that could not be resolved."
    }
    try {
        Expand-Archive -LiteralPath $terraformZip -DestinationPath $targetDir -Force
    }
    catch {
        "An error occurred that could not be resolved."
    }
    remove-item $terraformZip
}

$targetDir = $MyInvocation.InvocationName -replace "tfw.ps1"
$propFile  = $targetDir + "terraform-maven.properties"
$tfwProps  = convertfrom-stringdata (get-content $propFile -raw)

##################################
# Construct the URL of the package
##################################
$releaseSource = $tfwProps.distributionSite  +
                "/" + $tfwProps.releaseDir  +
                "/" + $tfwProps.releaseVer  +
                "/" + $tfwProps.releaseName +
                "_" + $tfwProps.releaseVer  +
                "_" + "windows"             +
                "_" + $tfwProps.releaseSuffix

$terraformBinary = $targetDir + "terraform.exe"

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
    if ($installedVerion -eq $tfwProps.releaseVer) {
        Write-Host ""
    } else {
        Write-Host "Different version, downloading"
        downloadTerraformBinary $releaseSource $targetDir
    }
} else {
    ############################################
    # if the terraform binary is not there,
    # download and install the desired version
    ############################################
    Write-Host "Terraform not found, installing"
    downloadTerraformBinary $releaseSource $targetDir
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
