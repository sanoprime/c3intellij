package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.c3lang.intellij.psi.C3LocalDeclAfterType

abstract class C3LocalDeclAfterTypeMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3LocalDeclAfterType {

    override fun getName(): String? = nameIdent

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentifier?.textOffset ?: super.getTextOffset()
    }

    override val nameIdentElement: LeafPsiElement?
        get() = firstChild as? LeafPsiElement

    override val nameIdent: String?
        get() = nameIdentElement?.text

//    override fun getReference(): PsiReference = C3ArgReference(this)
//
//    class C3ArgReference(
//        element: C3LocalDeclAfterType
//    ) : C3ReferenceBase<C3LocalDeclAfterType>(element) {
//
//        override fun multiResolve(): Collection<C3PsiElement> {
//            val compoundStatement = element.parentOfType<C3CompoundStatement>() ?: return emptyList()
//
//            val args = PsiTreeUtil.collectElements(compoundStatement) { psi ->
//                psi is C3Arg && element.name == psi.text
//            }.filterIsInstance<C3Arg>()
//
//            return args.toList()
//        }
//
//        override fun getRangeInElement(): TextRange {
//            val nameIdentifier = element.nameIdentifier ?: return TextRange.EMPTY_RANGE
//
//            return TextRange.create(
//                nameIdentifier.startOffset - element.textRange.startOffset,
//                nameIdentifier.textLength
//            )
//        }
//
//        override fun isReferenceTo(element: PsiElement): Boolean {
//            return element is C3Arg && super.isReferenceTo(element)
//        }
//    }
}