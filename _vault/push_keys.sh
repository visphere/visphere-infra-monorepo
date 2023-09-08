#!/bin/sh
#
# Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
# Silesian University of Technology
#
#     File name: push_keys.sh
#     Last modified: 9/8/23, 9:59 PM
#     Project name: moonsphere-infra-monorepo
#     Module name: moonsphere-infra-monorepo
#
# This project is a part of "MoonSphere" instant messenger system. This system is a part of
# completing an engineers degree in computer science at Silesian University of Technology.
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
# file except in compliance with the License. You may obtain a copy of the License at
#
#     <http://www.apache.org/license/LICENSE-2.0>
#
# Unless required by applicable law or agreed to in writing, software distributed under
# the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific language
# governing permissions and limitations under the license.
#

mounted_env_path="../../.env"
secret_name="moonsphere"

get_env_variable() {
    variable_name=$1
    if [ -f "$mounted_env_path" ]; then
        while IFS="=" read -r name value; do
            if [ "$name" = "$variable_name" ]; then
                echo "$value" | tr -d '\r'
                return
            fi
        done <"$mounted_env_path"
    fi
    echo ""
}

vault operator unseal "$(get_env_variable "MSPH_VAULT_UNSEAL_KEY")"

vault_token="$(get_env_variable "MSPH_VAULT_ROOT_TOKEN")"
export VAULT_TOKEN=$vault_token

vault secrets enable -path=$secret_name kv

vault kv put "$secret_name/msph-common/dev" \
    V_EUREKA_USERNAME="$(get_env_variable "MSPH_DEV_EUREKA_USERNAME")" \
    V_EUREKA_PASSWORD="$(get_env_variable "MSPH_DEV_EUREKA_PASSWORD")"

echo "Put V_EUREKA_USERNAME, V_EUREKA_PASSWORD in msph-common/dev"

vault kv put "$secret_name/msph-common/docker" \
    V_EUREKA_USERNAME="$(get_env_variable "MSPH_DOCKER_EUREKA_USERNAME")" \
    V_EUREKA_PASSWORD="$(get_env_variable "MSPH_DOCKER_EUREKA_PASSWORD")"

echo "Put V_EUREKA_USERNAME, V_EUREKA_PASSWORD in msph-common/dev"

vault kv put "$secret_name/msph-config-server" \
    V_SPRING_CONFIG_GIT_USERNAME="$(get_env_variable "MSPH_SPRING_CONFIG_GIT_USERNAME")" \
    V_SPRING_CONFIG_GIT_TOKEN="$(get_env_variable "MSPH_SPRING_CONFIG_GIT_TOKEN")"

echo "Put V_SPRING_CONFIG_GIT_USERNAME, V_SPRING_CONFIG_GIT_TOKEN in msph-config-server"

vault kv put "$secret_name/msph-account-service" \
    V_POSTGRES_USERNAME="$(get_env_variable "MSPH_ACCOUNT_POSTGRES_USERNAME")" \
    V_POSTGRES_PASSWORD="$(get_env_variable "MSPH_ACCOUNT_POSTGRES_PASSWORD")"

echo "Put V_POSTGRES_USERNAME, V_POSTGRES_PASSWORD in msph-account-service"

vault kv put "$secret_name/msph-auth-service" \
    V_POSTGRES_USERNAME="$(get_env_variable "MSPH_AUTH_POSTGRES_USERNAME")" \
    V_POSTGRES_PASSWORD="$(get_env_variable "MSPH_AUTH_POSTGRES_PASSWORD")"

echo "Put V_POSTGRES_USERNAME, V_POSTGRES_PASSWORD in msph-auth-service"

vault kv put "$secret_name/msph-misc-service" \
    V_CAPTCHA_SECRET_KEY="$(get_env_variable "MSPH_CAPTCHA_SECRET_KEY")" \
    V_CAPTCHA_SITE_KEY="$(get_env_variable "MSPH_CAPTCHA_SITE_KEY")"

echo "Put V_CAPTCHA_SECRET_KEY, V_CAPTCHA_SITE_KEY in msph-misc-service"
