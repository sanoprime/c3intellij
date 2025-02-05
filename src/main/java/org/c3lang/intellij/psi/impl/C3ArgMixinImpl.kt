package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.elementType
import com.intellij.psi.util.findTopmostParentOfType
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3ArgMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3Arg {

    override fun getNameIdentifier(): PsiElement = this

    override fun getName(): String = lastChild.text

    override fun setName(name: String): PsiElement {
        // TODO
        return this
    }

    override fun getReference(): PsiMultiReference = PsiMultiReference(
        arrayOf(LocalDeclAfterTypeReference(this)),
        this
    )

    private class LocalDeclAfterTypeReference(
        element: C3Arg
    ) : C3ReferenceBase<C3Arg>(element, element.expr) {

        override fun multiResolve(): Collection<C3PsiElement> {
            element.findTopmostParentOfType<C3CompoundStatement>()?.let {

                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
                    override fun isAccepted(other: PsiElement): Boolean {
                        val localDeclAfterType = other as? C3LocalDeclAfterType ?: return false
                        return localDeclAfterType.name == element.name && localDeclAfterType.textOffset < element.textOffset
                    }
                })

                return collectElements.filterIsInstance<C3PsiElement>()
            }

            return emptyList()
        }

    }
}