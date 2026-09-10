terraform {
  required_version = ">= 1.6.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 5.4"
    }
  }
}

provider "azurerm" {
  features {}
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "location" {
  type    = string
  default = "eastus"
}

resource "azurerm_resource_group" "dd" {
  name     = "rg-degree-dean-${var.environment}"
  location = var.location
}

resource "azurerm_container_registry" "dd" {
  name                = "acrdegreedean${var.environment}"
  resource_group_name = azurerm_resource_group.dd.name
  location            = azurerm_resource_group.dd.location
  sku                 = "Basic"
  admin_enabled       = false
}

resource "azurerm_log_analytics_workspace" "dd" {
  name                = "law-degree-dean-${var.environment}"
  location            = azurerm_resource_group.dd.location
  resource_group_name = azurerm_resource_group.dd.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_key_vault" "dd" {
  name                       = "kv-dd-${var.environment}"
  location                   = azurerm_resource_group.dd.location
  resource_group_name        = azurerm_resource_group.dd.name
  tenant_id                  = data.azurerm_client_config.current.tenant_id
  sku_name                   = "standard"
  purge_protection_enabled   = var.environment == "production"
  soft_delete_retention_days = 7
}

data "azurerm_client_config" "current" {}

output "resource_group" {
  value = azurerm_resource_group.dd.name
}
