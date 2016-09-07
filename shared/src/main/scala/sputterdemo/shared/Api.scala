package sputterdemo.shared

import sputter.shared.ContactApi
import sputter.shared.RegistrationApi
import sputter.shared.SimpleRegistrationForm

/**
  * The actual API implemented by the demo client and server.
  * todo: Create a new repo for the demos, with a new cross-project config.
  * Move this into the shared library of that repo.
  */
trait Api extends ContactApi with RegistrationApi

trait TypedApi extends Api {
  type Form = SimpleRegistrationForm
}
