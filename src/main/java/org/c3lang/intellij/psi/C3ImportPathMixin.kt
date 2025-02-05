package org.c3lang.intellij.psi

interface C3ImportPathMixin : C3PsiNamedElement, C3ModuleNamePsiElement {
    fun endsWith(path: C3Path): Boolean
}