package com.bawer.tasks.revolut.ewallet.controller

import ro.pippo.controller.Controller

fun Controller.getPagingParameters() = Pair(
        routeContext.getParameter("limit").toInt(10),
        routeContext.getParameter("after").toInt(0))