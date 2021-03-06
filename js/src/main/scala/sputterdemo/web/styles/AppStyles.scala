package sputterdemo.web.styles

import sputterdemo.web.components.TopNav.styles
import sri.scalacss.Defaults._

import scalacss.Defaults._
import scalacss.mutable.GlobalRegistry

object AppStyles {

  def load() = {
    GlobalRegistry.register(GlobalStyle, styles)
    GlobalRegistry.addToDocumentOnRegistration()
  }
}
