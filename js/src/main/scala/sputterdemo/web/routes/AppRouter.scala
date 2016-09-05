package sputterdemo.web.routes

import sputterdemo.web.components.TopNav
import sri.core.ReactElement
import sri.scalacss.Defaults._
import sputterdemo.web.screens.{ContactScreen, HomeScreen, RegistrationScreen}
import sputterdemo.web.styles.GlobalStyle
import sri.web.router._
import sri.web.vdom.htmltags._

object AppRouter {

  object HomePage extends WebStaticPage
  object RegistrationPage extends WebStaticPage
  object ContactPage extends WebStaticPage

  object Config extends WebRouterConfig {

    override val history: History = HistoryFactory.browserHistory()

    override val initialRoute: (WebStaticPage, WebRoute) = defineInitialRoute(HomePage, (route: WebRoute) => HomeScreen())

    override val notFound: WebRouteNotFound = WebRouteNotFound(HomePage)

    staticRoute(page = RegistrationPage, path = "registration", component = (route: WebRoute) => RegistrationScreen())
    staticRoute(page = ContactPage, path = "contact", component = (route: WebRoute) => ContactScreen())

    /**
     * this method is responsible for rendering components
     * @param route current route that is pushed to stack
     * @return
     */
    override def renderScene(route: WebRoute): ReactElement = {
      div(className = GlobalStyle.flexOneAndDirectionVertical)(
        TopNav(),
        super.renderScene(route)
      )
    }
  }

  val router = WebRouter(Config)
}
