package com.github.reschoene.exception

import javax.ws.rs.core.Response

class ValidationException(message: String?, var status: Response.Status? = null) : Exception(message)