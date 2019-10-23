#!/usr/bin/env bash
tee ./.databricks-connect >/dev/null <<EOF
{
  "host": "${DATABRICKS_ADDRESS}",
  "org_id": "0",
  "cluster_id": "${DATABRICKS_CLUSTER_ID}",
  "port": "15001",
  "token": "${DATABRICKS_API_TOKEN}"
}
EOF