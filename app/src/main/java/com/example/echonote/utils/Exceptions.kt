package com.example.echonote.utils

// Base EchoNote Exception used to catch any exception raised by a custom response
open class BaseEchoNoteException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

// Invalid state of application
class IllegalStateEchoNoteException(message: String? = null, cause: Throwable? = null) : BaseEchoNoteException(message, cause)

// Invalid argument provided but not empty argument (see EmptyArgumentEchoNoteException)
class IllegalArgumentEchoNoteException(message: String? = null, cause: Throwable? = null) : BaseEchoNoteException(message, cause)

// An argument was empty
class EmptyArgumentEchoNoteException(message: String? = null, cause: Throwable? = null) : BaseEchoNoteException(message, cause)

// Not found exception
class NotFoundEchoNoteException(message: String? = null, cause: Throwable? = null) : BaseEchoNoteException(message, cause)