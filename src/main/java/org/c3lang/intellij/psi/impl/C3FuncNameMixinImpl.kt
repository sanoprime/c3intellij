package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.c3lang.intellij.psi.C3FuncName

abstract class C3FuncNameMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3FuncName {

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun getName(): String {
        return text
    }

    override fun getNameIdentifier(): PsiElement {
        return this
    }

}