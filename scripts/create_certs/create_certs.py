import os
import time
import random
import sys
from OpenSSL import crypto

MOBILE_ROOT_CERT_PATH = 'elastica_ca.crt'
MOBILE_ROOT_CA_KEY_PATH = 'elastica_ca.key'


def create_user_cert(common_name, user_cert_folder_path):

    client_key = crypto.PKey()
    client_key.generate_key(crypto.TYPE_RSA, 2048)

    client_cert = crypto.X509()
    client_cert.set_version(2)
    client_cert.set_serial_number(random.randint(50000000, 100000000))

    client_subj = client_cert.get_subject()
    client_subj.commonName = common_name

    client_cert.add_extensions([
        crypto.X509Extension("basicConstraints", False, "CA:FALSE"),
        crypto.X509Extension("subjectKeyIdentifier", False, "hash", subject=client_cert),
    ])

    ca_cert = crypto.load_certificate(crypto.FILETYPE_PEM, open(MOBILE_ROOT_CERT_PATH, 'rb').read())
    ca_key = crypto.load_privatekey(crypto.FILETYPE_PEM, open(MOBILE_ROOT_CA_KEY_PATH, 'rb').read())

    client_cert.add_extensions([
        crypto.X509Extension("authorityKeyIdentifier", False, "keyid:always", issuer=ca_cert),
        crypto.X509Extension("extendedKeyUsage", False, "clientAuth"),
        crypto.X509Extension("keyUsage", False, "digitalSignature"),
    ])

    client_cert.set_issuer(ca_cert.get_subject())
    client_cert.set_pubkey(client_key)

    client_cert.gmtime_adj_notBefore(0)
    client_cert.gmtime_adj_notAfter(10 * 365 * 24 * 60 * 60)
    client_cert.sign(ca_key, 'sha256')

    # Save certificate
    with open(os.path.join(user_cert_folder_path, 'client2.pem'), "wt") as pem_file_name:
        pem_file_name.write(crypto.dump_certificate(crypto.FILETYPE_PEM, client_cert))

    # Save private key
    with open(os.path.join(user_cert_folder_path, 'client2.key'), "wt") as key_file_name:
        key_file_name.write(crypto.dump_privatekey(crypto.FILETYPE_PEM, client_key))


def main():
	create_user_cert("stress_test_user_2@elastica.co", "certs")
main()
