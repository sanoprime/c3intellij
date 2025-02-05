package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPostfix
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.c3lang.intellij.psi.C3ImportPath
import org.c3lang.intellij.psi.C3Path
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.ModuleName
import org.c3lang.intellij.psi.reference.C3ImportPathReference

abstract class C3ImportPathMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3ImportPath {

    override fun getNameIdentifier(): PsiElement? = this

    override fun setName(name: String): PsiElement {
        return this
    }

    override fun endsWith(path: C3Path): Boolean {
        return text.endsWith(path.text.dropPostfix("::"))
    }

//    override fun getReference(): PsiReference = SelfReference(this)

    override val moduleName: ModuleName?
        get() = ModuleName(text)

    class SelfReference(element: C3ImportPath) : PsiReferenceBase<C3ImportPath>(element) {
        override fun resolve(): PsiElement {
            return element
        }
    }
}

