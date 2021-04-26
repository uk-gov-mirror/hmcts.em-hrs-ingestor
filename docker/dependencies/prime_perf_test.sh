#one off job to populate large files from pauls computer

#echo to calculate md5 in base 64, run
#openssl dgst -md5 -binary filename.mp4 | openssl enc -base64


#create a file called "secret_envsettings.sh" with the primary connection string, obtained from the azure portal: https://portal.azure.com/#blade/Microsoft_Azure_Storage/ContainerMenuBlade/overview/storageAccountId/%2Fsubscriptions%2F1c4f0704-a29e-403d-b719-b90c34ef14c9%2FresourceGroups%2Fem-hrs-api-aat%2Fproviders%2FMicrosoft.Storage%2FstorageAccounts%2Femhrscvpaat/path/recordings/etag/%220x8D904C5CF021E7C%22/defaultEncryptionScope/%24account-encryption-key/denyEncryptionScopeOverride//defaultId//publicAccessVal/None
#it should have 1 line that reads like this:
#export CONNSTRING="DefaultEndpointsProtocol=https;AccountName=emhrscvpaat;...........


source docker/dependencies/secret_envsettings.sh
echo "connection string: $CONNSTRING"

#200M - once this has uploaded, clone it 9 times with a different segment number - this will produce 10 files of 200M
#az storage blob upload --content-md5 "H4pMd2A50GZh8F2ovbufjA==" -f ~/Desktop/audio_test-1hour-192kbs.mp4 -c recordings -n audiostream999000/FM-0111-testfile200M_2020-01-01-11.11.11.123-UTC_0.mp4 --connection-string "$CONNSTRING"


#
#
##1GIG
az storage blob upload --content-md5 "qyQ2rKG3WF3VwgHkybLYKw==" -f ~/Desktop/audio_test-5hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0222-testfile1GIG_2020-02-02-12.22.22.234-UTC_0.mp4 --connection-string "$CONNSTRING"
#
#
#
##2GIG
az storage blob upload --content-md5 "aBnlrPbO3v23QoSpe/oBrA==" -f ~/Desktop/audio_test-10hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0333-testfile2GIG_2020-03-03-13.33.33.345-UTC_0.mp4 --connection-string "$CONNSTRING"
#
#
##3GIG
az storage blob upload --content-md5 "EW/LzbT3e++7OLfpS2sfiw==" -f ~/Desktop/audio_test-15hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0444-testfile3GIG_2020-04-04-14.44.44.456-UTC_0.mp4 --connection-string "$CONNSTRING"
#
##4GIG
az storage blob upload --content-md5 "WeG+UMubON0b+1olefSbBg==" -f ~/Desktop/audio_test-20hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0555-testfile4GIG_2020-05-05-15.55.55.567-UTC_0.mp4 --connection-string "$CONNSTRING"
