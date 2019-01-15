package hangduykhiem.com.thesisocr.domain.interactor

import android.content.Context
import android.content.SharedPreferences
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.di.modules.AppModule.Companion.APP_CONTEXT
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.controller.LanguageSelectDialogController
import hangduykhiem.com.thesisocr.view.controller.SettingsController.Companion.SHARED_PREFERENCE
import javax.inject.Inject
import javax.inject.Named

class LanguageSelectInteractor @Inject constructor(
    @Named(APP_CONTEXT) var context: Context
) : Interactor<NoArg, LanguageSelectModel>() {

    companion object {
        val LANGUAGE_KEY = "LANGUAGE"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onAttach(restored: Boolean) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE)
        if (!restored) {
            updateModel(LanguageSelectModel(sharedPreferences.getStringSet(LANGUAGE_KEY, setOf())!!))
        }
    }

    override fun handleCommand(command: Command) {
        if (command is LanguageSelectDialogController.UpdateLanguageCommand) {
            sharedPreferences.edit().putStringSet(LANGUAGE_KEY, command.languages).apply()
        }
    }

}

data class LanguageSelectModel(
    val languageSet: Set<String>
) : Model