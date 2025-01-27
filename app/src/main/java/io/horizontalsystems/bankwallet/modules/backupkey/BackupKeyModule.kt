package io.horizontalsystems.bankwallet.modules.backupkey

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.entities.Account

object BackupKeyModule {
    const val ACCOUNT = "account"

    class Factory(private val account: Account) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val service = BackupKeyService(account, App.pinComponent)
            return BackupKeyViewModel(service) as T
        }
    }

    fun prepareParams(account: Account) = bundleOf(ACCOUNT to account)

}
