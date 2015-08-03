#!/usr/bin/env bash
curl http://localhost:7474/db/data/transaction/commit -d@data.json -H "Content-Type: application/json"
curl http://localhost:7474/db/data/transaction/commit -d@index.json -H "Content-Type: application/json"
echo
