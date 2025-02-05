package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiElementFilter
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.findTopmostParentOfType
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3ArgMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3Arg {

    override val nameIdentElement: LeafPsiElement?
        get() = expr?.let { it as? C3NameIdentProvider }?.nameIdentElement

    override val nameIdent: String?
        get() = nameIdentElement?.text

    private class LocalDeclAfterTypeReference(
        element: C3Arg
    ) : C3ReferenceBase<C3Arg>(element, element.expr) {

        override fun multiResolve(): Collection<C3PsiElement> {
            element.findTopmostParentOfType<C3CompoundStatement>()?.let {

                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
                    override fun isAccepted(other: PsiElement): Boolean {
                        val localDeclAfterType = other as? C3LocalDeclAfterType ?: return false
                        return localDeclAfterType.name == element.text/*name*/ && localDeclAfterType.textOffset < element.textOffset
                    }
                })

                return collectElements.filterIsInstance<C3PsiElement>()
            }
            return emptyList()
        }

    }
}