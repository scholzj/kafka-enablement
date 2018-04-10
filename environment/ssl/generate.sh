#!/usr/bin/env bash

PASSWORD="123456"

# Create dir
mkdir keys/

# Generate the CA
cfssl genkey -initca ca.json | cfssljson -bare keys/ca

# Generate server keys
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem server-0.json | cfssljson -bare keys/server-0
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem server-1.json | cfssljson -bare keys/server-1
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem server-2.json | cfssljson -bare keys/server-2

# Generate Connect keys - ext
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem connect-0.json | cfssljson -bare keys/connect-0
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem connect-1.json | cfssljson -bare keys/connect-1
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem connect-2.json | cfssljson -bare keys/connect-2

# Generate full chain server CRTs
cat keys/server-0.pem keys/ca.pem > keys/server-0-full-chain.pem
cat keys/server-1.pem keys/ca.pem > keys/server-1-full-chain.pem
cat keys/server-2.pem keys/ca.pem > keys/server-2-full-chain.pem

# Generate full chain Connecr CRTs
cat keys/connect-0.pem keys/ca.pem > keys/connect-0-full-chain.pem
cat keys/connect-1.pem keys/ca.pem > keys/connect-1-full-chain.pem
cat keys/connect-2.pem keys/ca.pem > keys/connect-2-full-chain.pem

# Generate user keys
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem user1.json | cfssljson -bare keys/user1
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem user2.json | cfssljson -bare keys/user2
cfssl gencert -ca keys/ca.pem -ca-key keys/ca-key.pem user-connect.json | cfssljson -bare keys/user-connect

# Convert CA to Java Keystore format (truststrore)
rm keys/truststore
keytool -importcert -keystore keys/truststore -storepass $PASSWORD -storetype JKS -alias ca -file keys/ca.pem -noprompt

# Convert keys to PKCS12
openssl pkcs12 -export -out keys/server-0.p12 -in keys/server-0-full-chain.pem -inkey keys/server-0-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/server-1.p12 -in keys/server-1-full-chain.pem -inkey keys/server-1-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/server-2.p12 -in keys/server-2-full-chain.pem -inkey keys/server-2-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/connect-0.p12 -in keys/connect-0-full-chain.pem -inkey keys/connect-0-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/connect-1.p12 -in keys/connect-1-full-chain.pem -inkey keys/connect-1-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/connect-2.p12 -in keys/connect-2-full-chain.pem -inkey keys/connect-2-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/user1.p12 -in keys/user1.pem -inkey keys/user1-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/user2.p12 -in keys/user2.pem -inkey keys/user2-key.pem -password pass:$PASSWORD
openssl pkcs12 -export -out keys/user-connect.p12 -in keys/user-connect.pem -inkey keys/user-connect-key.pem -password pass:$PASSWORD

# Convert PKCS12 keys to keystores
rm keys/*.keystore
keytool -importkeystore -srckeystore keys/server-0.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/server-0.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/server-1.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/server-1.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/server-2.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/server-2.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/connect-0.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/connect-0.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/connect-1.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/connect-1.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/connect-2.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/connect-2.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/user1.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/user1.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/user2.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/user2.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt
keytool -importkeystore -srckeystore keys/user-connect.p12 -srcstoretype PKCS12 -srcstorepass $PASSWORD -destkeystore keys/user-connect.keystore -deststoretype JKS -deststorepass $PASSWORD -noprompt