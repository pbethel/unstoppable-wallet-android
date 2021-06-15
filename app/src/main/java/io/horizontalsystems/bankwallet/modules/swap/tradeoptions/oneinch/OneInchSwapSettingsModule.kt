package io.horizontalsystems.bankwallet.modules.swap.tradeoptions.oneinch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.providers.Translator
import io.horizontalsystems.bankwallet.entities.Address
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.swap.oneinch.OneInchTradeService
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.AddressResolutionService
import io.horizontalsystems.bankwallet.modules.swap.tradeoptions.RecipientAddressViewModel
import java.math.BigDecimal

object OneInchSwapSettingsModule {

    val defaultSlippage = BigDecimal("0.5")

    data class OneInchSwapSettings(
            var slippage: BigDecimal = defaultSlippage,
            var gasPrice: Long? = null,
            var recipient: Address? = null
    )

    sealed class State {
        class Valid(val swapSettings: OneInchSwapSettings) : State()
        object Invalid : State()
    }

    class Factory(
            private val tradeService: OneInchTradeService,
            private val dex: SwapMainModule.Dex
    ) : ViewModelProvider.Factory {

        private val service by lazy { OneInchSettingsService(tradeService.swapSettings) }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val evmCoin = dex.coin ?: throw IllegalArgumentException()

            return when (modelClass) {
                OneInchSettingsViewModel::class.java -> OneInchSettingsViewModel(service, tradeService) as T
                RecipientAddressViewModel::class.java -> {
                    val addressParser = App.addressParserFactory.parser(evmCoin)
                    val resolutionService = AddressResolutionService(evmCoin.code, true)
                    val placeholder = Translator.getString(R.string.SwapSettings_RecipientPlaceholder)
                    RecipientAddressViewModel(service, resolutionService, addressParser, placeholder, listOf(service, resolutionService)) as T
                }
                else -> throw IllegalArgumentException()
            }
        }
    }
}