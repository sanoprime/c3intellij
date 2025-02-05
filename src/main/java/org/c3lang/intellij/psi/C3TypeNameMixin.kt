package org.c3lang.intellij.psi

import com.intellij.psi.StubBasedPsiElement
import org.c3lang.intellij.stubs.C3TypeEnum
import org.c3lang.intellij.stubs.C3TypeNameStub

interface C3TypeNameMixin : C3FullyQualifiedNamePsiElement, StubBasedPsiElement<C3TypeNameStub> {
    val typeEnum: C3TypeEnum
}