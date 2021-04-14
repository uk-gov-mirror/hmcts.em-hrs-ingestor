#as per https://blog.jongallant.com/2020/04/local-azure-storage-development-with-azurite-azuresdks-storage-explorer/


#sudo apt install libnss3-tools

#https://github.com/FiloSottile/mkcert/releases

#run
#mkcert -install
#Create certificate
#mkcert 127.0.0.1
#mkcert -CAROOT

#from ../volumes/keyvolumes
REQUESTS_CA_BUNDLE=`pwd`/rootCA.pem
#paul@local:~/git/em/em-hrs-ingestor/docker/volumes/azurite-keys-volume$ echo $REQUESTS_CA_BUNDLE
#/home/paul/git/em/em-hrs-ingestor/docker/volumes/azurite-keys-volume/rootCA.pem



#only do this once
#mkdir -p docker/volumes/azurite-keys-volume
#openssl req -newkey rsa:2048 -x509 -nodes -keyout docker/volumes/azurite-keys-volume/key.pem -new \
#        -out docker/volumes/azurite-keys-volume/cert.pem \
#        -sha256 -days 365 -addext "subjectAltName=IP:127.0.0.1" -subj "/C=CO/ST=ST/L=LO/O=OR/OU=OU/CN=CN"


#note to attach to this docker container, use something similar to this (check container name with docker container ps):
# docker container exec -it emhrsingestor_azure-storage-emulator-azurite-cvp_1 /bin/sh
