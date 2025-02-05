package org.c3lang.intellij.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.parentOfType
import org.c3lang.intellij.index.NameIndexService
import org.c3lang.intellij.psi.*
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3PathIdentMixinImpl(node: ASTNode) : C3PsiNamedElementImpl(node), C3PathIdent {

    override fun getName(): String? {
        return nameIdentifier?.text
    }

    override fun setName(name: String): PsiElement? {
        nameIdentElement?.replaceWithText(name)
        return this
    }

    override fun getNameIdentifier(): PsiElement? {
        return nameIdentElement
    }

    override fun getTextOffset(): Int {
        return nameIdentElement?.textOffset ?: super.getTextOffset()
    }

    override fun getTextRange(): TextRange? {
        return nameIdentElement?.textRange ?: super.getTextRange()
    }

    override val nameIdent: String?
        get() = nameIdentElement?.text

    override val nameIdentElement: LeafPsiElement?
        get() = lastChild as? LeafPsiElement

    override fun getReference(): PsiReference? {
        return PsiMultiReference(
            arrayOf(
                C3LocalDeclAfterTypeReference(this),
                C3ParameterReference(this),
                C3FuncNameReference(this),
            ),
            this
        )
    }

    private class C3LocalDeclAfterTypeReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val compoundStatement = element.parentOfType<C3CompoundStatement>() ?: return emptyList()

            val results = PsiTreeUtil.collectElementsOfType<C3LocalDeclAfterType>(
                compoundStatement,
                C3LocalDeclAfterType::class.java
            ).filter {
                it.textOffset < element.textOffset && it.nameIdent == element.nameIdent
            }

            return results
        }

    }

    private class C3ParameterReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            val functionDef = element.parentOfType<C3FuncDefinition>() ?: return emptyList()

            val parameters = PsiTreeUtil.collectElementsOfType<C3Parameter>(
                functionDef, C3Parameter::class.java
            )
            val results = parameters.filter {
                it.textOffset < element.textOffset && it.nameIdent == element.nameIdent
            }

            return results
        }
    }

    private class C3FuncNameReference(element: C3PathIdent) : C3ReferenceBase<C3PathIdent>(element) {
        override fun multiResolve(): Collection<C3PsiElement> {
            return NameIndexService.findByNameEndsWith(element.text, element.project)
                .filterIsInstance<C3CallablePsiElement>()
        }
    }

//    private class LocalDeclAfterTypeReference(element: C3PathIdent) :
//        C3ReferenceBase<C3PathIdent>(element, element.lastChild) {
//
//        override fun multiResolve(): Collection<C3PsiElement> {
//            val compoundStatement = element.parentOfType<C3CompoundStatement>() ?: return emptyList()
//
//            val collectElements = PsiTreeUtil.collectElements(compoundStatement) { it is C3LocalDeclAfterType }
//
//            val results = PsiTreeUtil.collectElementsOfType<C3LocalDeclAfterType>(compoundStatement, C3LocalDeclAfterType::class.java).filter {
//                it.textOffset < element.textOffset && it.nameIdent == element.nameIdent
//            }
//
//            return results
//        }
//    }

//    private class LocalDeclAfterTypeReference(element: C3PathIdent) :
//        C3ReferenceBase<C3PathIdent>(element, element.lastChild) {
//
//        override fun multiResolve(): Collection<C3PsiElement> {
//            element.findTopmostParentOfType<C3CompoundStatement>()?.let {
//
//                val collectElements = PsiTreeUtil.collectElements(it, object : PsiElementFilter {
//                    override fun isAccepted(other: PsiElement): Boolean {
//                        val localDeclAfterType = other as? C3LocalDeclAfterType ?: return false
//                        return localDeclAfterType.name == referenceNameElement?.text && localDeclAfterType.textOffset < element.textOffset
//                    }
//                })
//
//                return collectElements.filterIsInstance<C3PsiElement>()
//            }
//
//            return emptyList()
//        }
//    }
//
//    private class FuncDefReference(element: C3PathIdent) :
//        C3ReferenceBase<C3PathIdent>(element, element.nameIdentElement) {
//
//        override fun multiResolve(): Collection<C3PsiElement> {
//            val references = element.parent?.references ?: return emptyList()
//            val callName = element.nameIdent ?: return emptyList()
//
//            if (element.path == null) {
//                return NameIndexService.findByNameInSection(callName, element.importProvider)
//            }
//
//            val callables = references.mapNotNull {
//                it.resolve()
//            }.filterIsInstance<C3ImportPath>().flatMap {
//                NameIndexService.findByNameEndsWith(it, callName)
//            }/*.mapNotNull {
//                it.nameElement
//            }*/
//
//            return callables
//        }
//
//    }
}