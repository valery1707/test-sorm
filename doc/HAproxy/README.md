# Install HAProxy

More variants at https://haproxy.debian.net/

```bash
sudo add-apt-repository ppa:vbernat/haproxy-1.6
sudo apt-get update
sudo apt-get install haproxy
```

# Configure

Edit file: `/etc/haproxy/haproxy.cfg` and copy content from same file in current directory.

Restart: `sudo service haproxy restart`

# SSL

### Generate Self-signed certificate

sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -subj '/emailAddress=support@megatel.kz/CN=192.168.56.101/O=AstanaMegatel/ST=Karaganda/C=KZ' -keyout haproxy-ssl.key -out haproxy-ssl.crt

### Use certificate

cat haproxy-ssl.crt haproxy-ssl.key > haproxy-ssl.pem
sudo cp haproxy-ssl.pem /etc/ssl/private/
