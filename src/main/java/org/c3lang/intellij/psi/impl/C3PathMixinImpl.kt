package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.elementType
import org.c3lang.intellij.psi.C3Path
import org.c3lang.intellij.psi.C3Types

abstract class C3PathMixinImpl(node: ASTNode) : C3Path, C3PsiElementImpl(node) {

//    override fun getNameIdentifier(): PsiElement = this//firstChild.takeIf { it.elementType == C3Types.IDENT }
//
//    override fun getName(): String = nameIdentifier.text
//
//    override fun setName(name: String): PsiElement {
//        return this
//    }

//    override fun getTextOffset(): Int {
//        return nameIdentifier.textOffset
//    }

    override fun shorten() {
        val idents = node.getChildren(
            TokenSet.create(C3Types.IDENT, C3Types.SCOPE)
        ).toMutableList()

        if (idents.size <= 2) {
            // bar::
            return
        }

        idents.removeLast() /*::*/
        idents.removeLast() /*IDENT*/

        // remove std::
        deleteChildRange(
            idents.first().psi,
            idents.last().psi
        )
    }

//    class SelfPsiPolyVariantReferenceBase(val path: C3Path) :
//        PsiPolyVariantReferenceBase<C3Path>(path) {
//
//        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
//            val result = mutableListOf<PsiElement>(path)
//
//            element.parentOfType<C3PathIdentExpr>()?.let { pathIdentExpr ->
//                pathIdentExpr.importProvider.getImportOf(pathIdentExpr).forEach { importPath ->
//                    result.add(importPath)
//                }
//            }
//
//            return result.map {
//                PsiElementResolveResult(it)
//            }.toTypedArray()
//        }
//
//        override fun isReferenceTo(other: PsiElement): Boolean {
//            val pathIdentExpr = path.parentOfType<C3PathIdentExpr>()
//
//            if (pathIdentExpr != null) {
//                val otherPathIdent = other.parentOfType<C3PathIdentExpr>() ?: return false
//
//                return isReferenceTo(pathIdentExpr, otherPathIdent)
//            }
//
//            return false
//
//        }
//
//        private fun isReferenceTo(
//            source: C3PathIdentExpr,
//            other: C3PathIdentExpr
//        ): Boolean {
//            val path = source.pathIdent.path ?: return false
//
//            return path.importProvider.contains(source.pathIdent) && source.text == other.text
//        }
//    }

    /*class SelfReference(element: C3Path) : PsiReferenceBase<C3Path>(element) {
        override fun resolve(): C3Path? {
            return element
        }

        override fun getRangeInElement(): TextRange {
            return element.textRangeInParent
        }
    }*/

    /*class SelfPolyReference(val path: C3Path) : PsiPolyVariantReferenceBase<C3Path>(path) {
        override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult?> {
            element.parentOfType<C3CompoundStatement>()?.let {
                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
                    override fun isAccepted(other: PsiElement): Boolean {
                        other as? C3Path ?: return false

                        val source = path.parentOfType<C3PathIdentExpr>() ?: return false
                        val target = other.parentOfType<C3PathIdentExpr>() ?: return false

                        return source.text == target.text && path.importProvider.contains(source.pathIdent)
                    }
                })

                return collectElements.map { PsiElementResolveResult(it) }.toTypedArray()
            }
            return emptyArray()
        }

        override fun isSoft(): Boolean = true

    }*/

//    class SelfReference(element: C3Path) : PsiReferenceBase<C3Path>(element) {
//        override fun resolve(): C3Path? {
//            return element
//        }
//
//        override fun isReferenceTo(other: PsiElement): Boolean {
//            return element.isReferenceTo(other)
//        }
//
//        companion object {
//            private fun PsiElement.isReferenceTo(other: PsiElement): Boolean {
//                val path = this as? C3Path ?: return false
//                val pathIdentExpr = path.parentOfType<C3PathIdentExpr>()
//
//                if (pathIdentExpr != null) {
//                    val otherPathIdent = other.parentOfType<C3PathIdentExpr>() ?: return false
//
//                    return isReferenceTo(pathIdentExpr, otherPathIdent)
//                }
//
//                return false
//            }
//
//            private fun isReferenceTo(
//                source: C3PathIdentExpr,
//                other: C3PathIdentExpr
//            ): Boolean {
//
//                val path = source.pathIdent.path ?: return false
//
//                return path.importProvider.contains(source.pathIdent) && source.text == other.text
//            }
//        }
//    }

    /**/
}


