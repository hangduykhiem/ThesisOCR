package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.controller.ToMainTabsTransition
import hangduykhiem.com.thesisocr.view.controller.ToSplashControllerTransition
import javax.inject.Inject

class RootInteractor @Inject constructor() : Interactor<NoArg, NoModel>() {

    override fun onAttach(restored: Boolean) {
        if (!restored) {
            navigate(ToSplashControllerTransition)
        }
    }

}