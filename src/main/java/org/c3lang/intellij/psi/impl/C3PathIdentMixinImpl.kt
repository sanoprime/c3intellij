package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.findTopmostParentOfType
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3PathIdentMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3PathIdent {

    override val nameIdent: String?
        get() = lastChild.takeIf { it.node.elementType == C3Types.IDENT }?.text

    override fun getReference(): PsiReference? {
        return LocalDeclAfterTypeReference(this)
    }

    private class LocalDeclAfterTypeReference(element: C3PathIdent) :
        C3ReferenceBase<C3PathIdent>(element, element.lastChild) {

        override fun multiResolve(): Collection<C3PsiElement> {
            element.findTopmostParentOfType<C3CompoundStatement>()?.let {

                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
                    override fun isAccepted(other: PsiElement): Boolean {
                        val localDeclAfterType = other as? C3LocalDeclAfterType ?: return false
                        return localDeclAfterType.name == referenceNameElement?.text && localDeclAfterType.textOffset < element.textOffset
                    }
                })

                return collectElements.filterIsInstance<C3PsiElement>()
            }

            return emptyList()
        }
    }
}