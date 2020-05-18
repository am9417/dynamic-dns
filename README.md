# Dynamic DNS client
Java-based simple dynamic DNS client

## Introduction
Your domain provider may have a URL which you can open in a browser to update the domain to point to the correct IP if it has changed. This client periodically checks your public IP with an online IP service and updates the IP by making a GET request to the URL with domain name, a password and the new IP address in GET parameters.


## Usage:
1. Configure `default.conf` file. It contains a simple template with guidance how to configure.
2. Launch with `java DynamicDNS` and keep running e.g. in Linux with `screen`.

## Requirements:

* Java 7+
