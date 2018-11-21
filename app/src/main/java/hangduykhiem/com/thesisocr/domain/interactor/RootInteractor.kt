package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Args
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.helper.NoModel
import hangduykhiem.com.thesisocr.view.controller.ToMainControllerTransition
import javax.inject.Inject

class RootInteractor @Inject constructor() : Interactor<NoArg, NoModel>() {

    override fun onAttach(restored: Boolean) {
        if (!restored) {
            navigate(ToMainControllerTransition)
        }
    }

}