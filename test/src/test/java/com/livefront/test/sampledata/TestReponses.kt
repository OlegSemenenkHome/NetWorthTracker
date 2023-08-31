package com.livefront.test.sampledata

import com.networthtracker.data.CryptoAsset
import com.networthtracker.data.ImageLinks
import com.networthtracker.data.ListAsset
import com.networthtracker.data.PriceResult
import com.networthtracker.data.StockAsset
import com.networthtracker.data.Ticker
import com.networthtracker.data.room.AssetType

internal val sampleStockAssetList =
    listOf(
        StockAsset(
            "DRIL-QUIP INC", "DRQ"
        ),
        StockAsset(
            "CLEAR SECURE INC -CLASS A", "XNYS"
        )
    )

internal val sampleCryptoAssetList =
    listOf(
        CryptoAsset(
            symbol = "BTC",
            name = "Bitcoin",
            image = ImageLinks(
                thumb = "btc_thumb.jpg",
                small = "btc_small.jpg",
                large = "btc_large.jpg"
            ),
            tickers = listOf(
                Ticker(last = 47000.0),
                Ticker(last = 47500.0),
                Ticker(last = 46500.0)
            )
        ),
        CryptoAsset(
            symbol = "ETH",
            name = "Ethereum",
            image = ImageLinks(
                thumb = "eth_thumb.jpg",
                small = "eth_small.jpg",
                large = "eth_large.jpg"
            ),
            tickers = listOf(
                Ticker(last = 3400.0),
                Ticker(last = 3350.0),
                Ticker(last = 3420.0)
            )
        )
    )

internal val SampleCryptoAsset =
    CryptoAsset(
        symbol = "BTC",
        name = "Bitcoin",
        image = ImageLinks(
            thumb = "btc_thumb.jpg",
            small = "btc_small.jpg",
            large = "btc_large.jpg"
        ),
        tickers = listOf(
            Ticker(last = 47000.0),
            Ticker(last = 47500.0),
            Ticker(last = 46500.0)
        )
    )

internal var sampleCryptoListAsset = listOf(
    ListAsset("1", "BTC", "Bitcoin"),
    ListAsset("2", "ETH", "Ethereum"),
    ListAsset("3", "XRP", "Ripple"),
)

internal val sampleStockListAssetList =
    listOf(
        ListAsset("DRQ", "DRQ", "DRIL-QUIP INC", AssetType.STOCK),
        ListAsset("XNYS", "XNYS", "CLEAR SECURE INC -CLASS A", AssetType.STOCK)
    )

internal var sampleStockPriceResult = PriceResult(200.0)
