package bo.edu.umsa.backend.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class HttpUtil {

    companion object {
        private fun getRequestContext(): HttpServletRequest? {
            val requestAttributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
            return requestAttributes?.request
        }

        fun getRequestHost(): String? {
            return getRequestContext()?.remoteHost
        }

    }
}
