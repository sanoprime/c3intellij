package org.c3lang.intellij.psi

interface C3OptionalTypeProvider : C3PsiElement {
    val optionalTypeFQN : FullyQualifiedName?
}