#one off job to populate large files from pauls computer

source docker/dependencies/secret_envsettings.sh
echo "connection string: $CONNSTRING"

#200M
#az storage blob upload -f ~/Desktop/audio_test-1hour-192kbs.mp4 -c recordings -n audiostream999000/FM-0111-testfile200M_2020-01-01-11.11.11.123-UTC_0.mp4 --connection-string "$CONNSTRING"




#
#
##1GIG
#az storage blob upload -f ~/Desktop/audio_test-5hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0222-testfile1GIG_2020-02-02-12.22.22.234-UTC_0.mp4 --connection-string "$CONNSTRING"
#
#
#
##2GIG
#az storage blob upload -f ~/Desktop/audio_test-10hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0333-testfile2GIG_2020-03-03-13.33.33.345-UTC_0.mp4 --connection-string "$CONNSTRING"
#
#
##3GIG
#az storage blob upload -f ~/Desktop/audio_test-15hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0444-testfile3GIG_2020-04-04-14.44.44.456-UTC_0.mp4 --connection-string "$CONNSTRING"
#
##4GIG
#az storage blob upload -f ~/Desktop/audio_test-20hours-192kbs.mp4 -c recordings -n audiostream999000/FM-0555-testfile4GIG_2020-05-05-15.55.55.567-UTC_0.mp4 --connection-string "$CONNSTRING"
