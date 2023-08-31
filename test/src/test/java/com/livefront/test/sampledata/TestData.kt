package com.livefront.test.sampledata

import com.networthtracker.data.ListAsset
import com.networthtracker.data.room.Asset
import com.networthtracker.data.room.AssetType

internal var sampleCryptoAsset =
    Asset("BTCCRYPTO", "Bitcoin", "btc_large.jpg", "46500.0", "0", "BTC", "BTC", AssetType.CRYPTO)

internal var sampleAssetStock =
    Asset("AAPLSTOCK", "Apple Inc", "", "200.0", "0", "AAPL", "", AssetType.STOCK)

internal var sampleStockListAsset = listOf(
    ListAsset("1", "AAPL", "Apple Inc", AssetType.STOCK),
    ListAsset("2", "MSFT", "Microsoft Corporation", AssetType.STOCK),
    ListAsset("3", "GOOGL", "Alphabet Inc. (Google)", AssetType.STOCK),
    ListAsset("4", "AMZN", "Amazon.com Inc."),
    ListAsset("5", "FB", "Meta Platforms Inc. (Facebook)", AssetType.STOCK),
    ListAsset("6", "TSLA", "Tesla Inc.", AssetType.STOCK),
    ListAsset("7", "JPM", "JPMorgan Chase & Co.", AssetType.STOCK),
    ListAsset("8", "V", "Visa Inc.", AssetType.STOCK),
    ListAsset("9", "NVDA", "NVIDIA Corporation", AssetType.STOCK),
    ListAsset("10", "TLSA", "Tesla Inc.", AssetType.STOCK)
)

internal var sampleUserAssetList = listOf(
    ListAsset("11", "BTC", "Bitcoin"),
    ListAsset("12", "ETH", "Ethereum"),
    ListAsset("13", "XRP", "Ripple"),
    ListAsset("1", "AAPL", "Apple Inc", AssetType.STOCK),
    ListAsset("2", "MSFT", "Microsoft Corporation", AssetType.STOCK),
    ListAsset("3", "GOOGL", "Alphabet Inc. (Google)", AssetType.STOCK),
    ListAsset("4", "AMZN", "Amazon.com Inc."),
    ListAsset("5", "FB", "Meta Platforms Inc. (Facebook)", AssetType.STOCK),
    ListAsset("6", "TSLA", "Tesla Inc.", AssetType.STOCK),
    ListAsset("7", "JPM", "JPMorgan Chase & Co.", AssetType.STOCK),
    ListAsset("8", "V", "Visa Inc.", AssetType.STOCK),
    ListAsset("9", "NVDA", "NVIDIA Corporation", AssetType.STOCK),
    ListAsset("10", "TLSA", "Tesla Inc.", AssetType.STOCK)
)


